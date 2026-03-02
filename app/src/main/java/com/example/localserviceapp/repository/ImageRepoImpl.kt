package com.example.localserviceapp.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ImageRepoImpl : ImageRepo {

    override fun uploadImage(imageUri: Uri): Flow<Result<String>> = callbackFlow {
        MediaManager.get().upload(imageUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    // Not implemented
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    // Not implemented
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val secureUrl = resultData?.get("secure_url") as? String
                    if (secureUrl != null) {
                        trySend(Result.success(secureUrl))
                    } else {
                        trySend(Result.failure(Exception("Image upload failed: Secure URL not found.")))
                    }
                    close()
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    trySend(Result.failure(Exception("Image upload failed: ${error?.description}")))
                    close()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    // Not implemented
                }
            })
            .dispatch()

        awaitClose { /* No-op */ }
    }
}
