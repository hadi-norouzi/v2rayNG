package com.v2ray.ang.ui.subscription.edit

import com.v2ray.ang.dto.SubscriptionItem

sealed class SubscriptionState {
    class Updated(item: SubscriptionItem): SubscriptionState()
    class Added(item: SubscriptionItem): SubscriptionState()

    object Initial: SubscriptionState()

    object Failed: SubscriptionState()
}
