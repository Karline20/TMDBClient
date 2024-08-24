package coding.legaspi.tmdbclient.utils

import coding.legaspi.tmdbclient.data.model.error.Error

interface DialogHelper {

    fun showUnauthorized(error: Error)

    fun showError(error: Error)

    fun showSuccess(title: String, content: String)

    fun showEmailVerification(title: String, content: String)

    fun showLogout(
        title: String,
        content: String,
        confirm: String,
        cancel: String,
        callback: (Boolean) -> Unit
    )

    fun thanksSuccess(title: String, content: String, callback: (Boolean) -> Unit)

    fun already(title: String, content: String)

    fun tutorial(title: String,
                 content: String,
                 confirm: String,
                 cancel: String,
                 callback: (Boolean) -> Unit)

    fun wrong(title: String,
              content: String,
              confirm: String,
              cancel: String,
              callback: (Boolean) -> Unit)

    fun delete(title: String,
              content: String,
              confirm: String,
              cancel: String,
              callback: (Boolean) -> Unit)

    fun connection(title: String,
              content: String,
              confirm: String,
              callback: (Boolean) -> Unit)

    fun showUnauthorizedError(error: Error, positiveButtonFunction: () -> Unit)
}