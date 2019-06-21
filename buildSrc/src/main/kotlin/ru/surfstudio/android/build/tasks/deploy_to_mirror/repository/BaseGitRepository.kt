package ru.surfstudio.android.build.tasks.deploy_to_mirror.repository

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit

/**
 * Parent class for git repository
 */
abstract class BaseGitRepository {

    protected abstract val repository: Repository
    protected abstract val repositoryName: String

    protected val git by lazy { Git(repository) }

    /**
     * Delete repository
     */
    open fun delete() = repository.close()

    /**
     * Get [RevCommit] by hash
     */
    fun getRevCommit(commitHash: String): RevCommit = git.log()
            .add(ObjectId.fromString(commitHash))
            .setMaxCount(1)
            .call()
            .first()

    /**
     * Get all branches
     */
    fun getAllBranches(): List<Ref> = git.branchList().call()
}