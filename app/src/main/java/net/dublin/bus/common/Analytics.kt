package net.dublin.bus.common


import android.app.Activity
import android.content.Context
import android.os.Bundle

import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess
import net.dublin.bus.ui.view.search.SearchActivity

object Analytics {
    private interface Constants {
        companion object {
            const val FAVORITE = "favorite"
            const val FAVORITE_NO = "favorite_yes"
            const val FAVORITE_YES = "favorite_no"

            const val BUTTON_LIST_MAP = "button_list_map"
            const val BUTTON_LIST = "list"
            const val BUTTON_MAP = "map"

            const val MENU = "menu"
            const val MENU_TIMETABLES = "Timetables"

            const val CLICK_LONG = "click_long"
            const val CLICK_LONG_TIMETABLES = "Timetables"

            const val QUANTITY_FAVOURITES = "quantity_favourites"

            const val SCREEN_FAVOURITES = "FavouritesFragment"
            const val SCREEN_ROUTES = "RoutesFragment"
            const val SCREEN_STOPS = "StopsFragment"

            const val ROUTE_ORIGIN = "route_origin"
            const val ROUTE_ORIGIN_NEAR = "near_screen"
            const val ROUTE_ORIGIN_DETAIL_MAP = "detail_map_screen"
            const val ROUTE_ORIGIN_DETAIL_LIST = "detail_list_screen"
            const val ROUTE_ORIGIN_SEARCH = "search_screen"
            const val ROUTE_ORIGIN_SCREEN = "route_screen"
            const val ROUTE_ORIGIN_FAVOURITE = "favourite_screen"

            const val SYNC_STATUS = "sync_status"
            const val SYNC_SUCCESS = "sync_success"
            const val SYNC_ERROR = "sync_error"
        }
    }

    fun sendFavoriteEvent(context: Context, statusFavorite: Boolean) {
        val action: String = if (statusFavorite) {
            Constants.FAVORITE_NO
        } else {
            Constants.FAVORITE_YES
        }

        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.VALUE, action)
        FirebaseAnalytics.getInstance(context).logEvent(Constants.FAVORITE, params)
    }

    fun sendListOrMapEvent(context: Context, isMap: Boolean) {
        val action: String = if (isMap) {
            Constants.BUTTON_MAP
        } else {
            Constants.BUTTON_LIST
        }

        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.VALUE, action)
        FirebaseAnalytics.getInstance(context).logEvent(Constants.BUTTON_LIST_MAP, params)
    }

    fun sendSyncStatus(context: Context, success: Boolean) {
        val action: String = if (success) {
            Constants.SYNC_SUCCESS
        } else {
            Constants.SYNC_ERROR
        }

        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.VALUE, action)
        FirebaseAnalytics.getInstance(context).logEvent(Constants.SYNC_STATUS, params)
    }

    fun sendMenuTimetablesEvent(context: Context) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.VALUE, Constants.MENU_TIMETABLES)
        FirebaseAnalytics.getInstance(context).logEvent(Constants.MENU, params)
    }

    fun sendClickLongTimetablesEvent(context: Context) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.VALUE, Constants.CLICK_LONG_TIMETABLES)
        FirebaseAnalytics.getInstance(context).logEvent(Constants.CLICK_LONG, params)
    }

    fun sendMapOrListProperty(context: Context, isMap: Boolean) {
        val action: String = if (isMap) {
            Constants.BUTTON_MAP
        } else {
            Constants.BUTTON_LIST
        }

        FirebaseAnalytics.getInstance(context).setUserProperty(Constants.BUTTON_LIST_MAP, action)
    }

    fun sendRouteFavouriteEvent(context: Context?) {
        sendRouteOriginEvent(context, Constants.ROUTE_ORIGIN_FAVOURITE)
    }


    fun sendRouteEvent(context: Context?) {
        sendRouteOriginEvent(context, Constants.ROUTE_ORIGIN_SCREEN)
    }

    fun sendRouteNearEvent(context: Context) {
        sendRouteOriginEvent(context, Constants.ROUTE_ORIGIN_NEAR)
    }

    fun sendRouteDetailMapEvent(context: Context) {
        sendRouteOriginEvent(context, Constants.ROUTE_ORIGIN_DETAIL_MAP)
    }

    fun sendRouteDetailListEvent(context: Context?) {
        sendRouteOriginEvent(context, Constants.ROUTE_ORIGIN_DETAIL_LIST)
    }

    fun sendRouteSearchEvent(context: Context?) {
        sendRouteOriginEvent(context, Constants.ROUTE_ORIGIN_SEARCH)
    }

    fun sendRouteOriginEvent(context: Context?, origin: String) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.VALUE, origin)
        FirebaseAnalytics.getInstance(context).logEvent(Constants.ROUTE_ORIGIN, params)
    }

    fun sendFavouritesQtdProperty(context: Context, quantity: Int) {
        FirebaseAnalytics.getInstance(context).setUserProperty(Constants.QUANTITY_FAVOURITES, quantity.toString())
    }

    private fun trackScreen(activity: Activity, name: String) {
        FirebaseAnalytics.getInstance(activity).setCurrentScreen(activity, null, name)
    }

    fun trackScreenFavourites(activity: Activity) {
        trackScreen(activity, Constants.SCREEN_FAVOURITES)
    }

    fun trackScreenRoutes(activity: Activity) {
        trackScreen(activity, Constants.SCREEN_ROUTES)
    }

    fun trackScreenStops(activity: Activity) {
        trackScreen(activity, Constants.SCREEN_STOPS)
    }
}
