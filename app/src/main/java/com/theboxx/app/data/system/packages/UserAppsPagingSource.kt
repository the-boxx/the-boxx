package com.theboxx.app.data.system.packages

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState

class UserAppsPagingSource(private val context: Context, private val apps: List<UserApps>) : PagingSource<Int, UserApps>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserApps> {
        return try {
            val pageIndex = params.key ?: 0
            val pageSize = 30

            val startIndex = pageIndex * pageSize
            val endIndex = minOf(startIndex + pageSize, apps.size)

            val appsForPage = if (startIndex < apps.size) {
                apps.subList(startIndex, endIndex)
            } else {
                emptyList()
            }


            LoadResult.Page(
                data = appsForPage,
                prevKey = if (pageIndex > 0) pageIndex - 1 else null,
                nextKey = if (appsForPage.isNotEmpty() && endIndex < apps.size) pageIndex + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserApps>): Int? {
        return state.anchorPosition
    }

}