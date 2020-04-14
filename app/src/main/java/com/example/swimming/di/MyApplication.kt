package com.example.swimming.di

import android.app.Application
import com.example.swimming.data.board.BoardDataSource
import com.example.swimming.data.board.BoardRepository
import com.example.swimming.data.map.MapDataSource
import com.example.swimming.data.map.MapRepository
import com.example.swimming.data.profile.ProfileDataSource
import com.example.swimming.data.profile.ProfileRepository
import com.example.swimming.data.user.UserDataSource
import com.example.swimming.data.user.UserRepository
import com.example.swimming.ui.board.BoardViewModel
import com.example.swimming.ui.board.BoardViewModelFactory
import com.example.swimming.ui.map.MapViewModel
import com.example.swimming.ui.map.MapViewModelFactory
import com.example.swimming.ui.profile.ProfileViewModel
import com.example.swimming.ui.profile.ProfileViewModelFactory
import com.example.swimming.ui.user.UserViewModel
import com.example.swimming.ui.user.UserViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

// 의존성 주입
class MyApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))

        bind() from singleton { UserDataSource() }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from provider { UserViewModel(instance()) }
        bind() from provider { UserViewModelFactory(instance()) }

        bind() from singleton { BoardDataSource() }
        bind() from singleton { BoardRepository(instance(), instance()) }
        bind() from provider { BoardViewModel(instance()) }
        bind() from provider { BoardViewModelFactory(instance()) }

        bind() from singleton { ProfileDataSource() }
        bind() from singleton { ProfileRepository(instance(), instance()) }
        bind() from provider { ProfileViewModel(instance()) }
        bind() from provider { ProfileViewModelFactory(instance()) }

        bind() from singleton { MapDataSource() }
        bind() from singleton { MapRepository(instance(), instance()) }
        bind() from provider { MapViewModel(instance()) }
        bind() from provider { MapViewModelFactory(instance()) }
    }
}