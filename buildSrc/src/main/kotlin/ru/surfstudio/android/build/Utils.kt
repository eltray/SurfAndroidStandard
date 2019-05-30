package ru.surfstudio.android.build

import groovy.lang.MissingPropertyException
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import ru.surfstudio.android.build.model.ProjectSnapshot

const val EMPTY_STRING = ""
const val EMPTY_INT = -1

private const val PROJECT_POSTFIX_KEY = "projectPostfix"
private const val PROJECT_VERSION_KEY = "projectVersion"

/**
 * Get project snapshot information
 */
fun Project.getProjectSnapshot() = ProjectSnapshot(
        readProperty(PROJECT_POSTFIX_KEY, EMPTY_STRING),
        readProperty(PROJECT_VERSION_KEY, EMPTY_INT)
)

/**
 * Read property from Project without Exception
 */
private fun <T> Project.readProperty(name: String, defValue: T): T {
    try {
        return property(name) as? T ?: defValue
    } catch (e: MissingPropertyException) {
        //Missing property
    } catch (e: UnknownProjectException) {
        //Missing property
    }
    return defValue
}