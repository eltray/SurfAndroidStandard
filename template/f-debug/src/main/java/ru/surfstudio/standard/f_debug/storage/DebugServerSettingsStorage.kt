package ru.surfstudio.standard.f_debug.storage

import android.content.SharedPreferences
import ru.surfstudio.android.dagger.scope.PerApplication
import ru.surfstudio.android.shared.pref.NO_BACKUP_SHARED_PREF
import ru.surfstudio.android.shared.pref.SettingsUtil
import javax.inject.Inject
import javax.inject.Named
private const val IS_CHUCK_ENABLED_KEY = "IS_CHUCK_ENABLED_KEY"
private const val IS_TEST_SERVER_ENABLED = "IS_TEST_SERVER_ENABLED"
@PerApplication
class DebugServerSettingsStorage @Inject constructor(
        @Named(NO_BACKUP_SHARED_PREF) private val noBackupSharedPref: SharedPreferences
) {

    var isChuckEnabled: Boolean
        get() = SettingsUtil.getBoolean(noBackupSharedPref, IS_CHUCK_ENABLED_KEY, false)
        set(value) = SettingsUtil.putBoolean(noBackupSharedPref, IS_CHUCK_ENABLED_KEY, value)

    var isTestServerEnabled: Boolean
        get() = SettingsUtil.getBoolean(noBackupSharedPref, IS_TEST_SERVER_ENABLED, true)
        set(value) = SettingsUtil.putBoolean(noBackupSharedPref, IS_TEST_SERVER_ENABLED, value)
}