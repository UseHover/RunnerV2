package com.hover.runner.customViews.detailsTopLayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hover.runner.R
import com.hover.runner.actions.ActionStatusEnum
import com.hover.runner.actions.models.Action
import com.hover.runner.utils.RunnerColor

 abstract  class TopDetailsContentChooser(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet),
     TopDetailsContentChooseInterface {
    override fun getLayoutBackground(status: String) : Int {
       return when (status) {
            "PENDING" -> RunnerColor(context).YELLOW
            "FAILED" -> RunnerColor(context).RED
            "SUCCEEDED" -> RunnerColor(context).GREEN
            else -> RunnerColor(context).DARK
        }
    }

     override fun getTitleTextColor(status: String): Int {
         return if(status == Action.statusToString(ActionStatusEnum.NOT_YET_RUN)) RunnerColor(context).WHITE
         else RunnerColor(context).DARK
     }

     override fun getTitleTextCompoundDrawable(status: String): Int {
         return if(status == Action.statusToString(ActionStatusEnum.NOT_YET_RUN)) R.drawable.ic_arrow_back_white_24dp
         else 0
     }

     override fun getSubTitleTextColor(status: String): Int {
         return getTitleTextColor(status)
     }

     override fun getDescTitle(status: String, detailScreenType: DetailScreenType) : Int {
        return if(detailScreenType == DetailScreenType.ACTION) {
           when(status) {
               "PENDING" -> R.string.pendingStatus_title
               "FAILED" -> R.string.failedStatus_title
               else -> R.string.successStatus_title
           }
       } else { // TRANSACTION DETAILS
          when (status) {
               "PENDING" -> R.string.transaction_det_pending
               "FAILED" -> R.string.transaction_det_failed
                else -> R.string.transaction_det_success
           }
       }
    }

    override fun getDescContent(status: String, detailScreenType: DetailScreenType) : Int {
        return if(detailScreenType == DetailScreenType.ACTION) {
            when(status) {
                "PENDING" -> R.string.pendingStatus_desc
                "FAILED" -> R.string.failedStatus_desc
                else -> R.string.successStatus_title
            }
        }
        else when(status) { // TRANSACTION DETAILS
            "PENDING" -> R.string.pendingStatus_desc
            "FAILED" -> R.string.failedStatus_desc
            else -> R.string.successStatus_title
        }
    }

     override fun getDescCompoundDrawable(status: String) : Int {
         return when(status) {
             "PENDING" -> R.drawable.ic_warning_black_24dp
             "FAILED" -> R.drawable.ic_error_black_24dp
             else ->R.drawable.ic_check_circle_black_24dp
         }
     }

     override fun getDescVisibility(status: String): Int {
         return if(status == "SUCCEEDED") View.GONE else View.VISIBLE
     }

     override fun getDescLinkLabel(status: String)  : Int {
            return when(status) {
                "PENDING" -> R.string.pendingStatus_linkText
                "FAILED" -> R.string.failedStatus_linkText
                else -> R.string.success_label
            }
    }

    override  fun getLink() : Int {
        return R.string.pendingStatus_url
    }

    override fun getWebTitle(status: String) : Int {
        return when(status) {
            "PENDING" -> R.string.pending_transaction
            else-> R.string.failed_transaction
        }
    }
}