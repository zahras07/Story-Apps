package com.aya.storyapps2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aya.storyapps2.dataclass.User
import com.aya.storyapps2.responses.ListStoryItem
import com.aya.storyapps2.responses.MapsList
import com.aya.storyapps2.responses.Story

object Dummy {
    fun generateDummyStory(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val user = ListStoryItem(
                i.toString(),
                "photo $i",
                "created $i",
                "name $i",
                "desc $i"
            )
            items.add(user)
        }
        return items
    }

    fun generateUser(): LiveData<User> {
        val mutable = MutableLiveData<User>()
        val user = User("juuic", true)
        mutable.value = user
        return mutable

    }

    fun generateDummyDetail(): Story {
        val story = Story(
            "akkbs",
            "uuby",
            "wwlan",
            "wpqmn",
            "aajxh"
        )

        return story

    }

    fun generateDummyMaps(): List<MapsList>{
        val maps : MutableList<MapsList> = arrayListOf()
        for (i in 0..100) {
            val data = MapsList(
                "name $i",
                "desc $i",
                1.0,
                1.0
            )
            maps.add(data)
        }
        return maps

    }
}