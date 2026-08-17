package com.example.paxrioverde.util

import com.example.paxrioverde.AndroidContext
import com.google.android.play.core.review.ReviewManagerFactory

actual class ReviewManager actual constructor() {
    actual fun requestReview() {
        val context = AndroidContext.get()
        val activity = AndroidContext.getActivity() ?: return
        
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()
        
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}
