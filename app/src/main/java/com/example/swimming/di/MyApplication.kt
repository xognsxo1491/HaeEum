package com.example.swimming.di

import android.app.Application
import com.example.swimming.data.board.BoardDataSource
import com.example.swimming.data.board.BoardRepository
import com.example.swimming.data.user.UserDataSource
import com.example.swimming.data.user.UserRepository
import com.example.swimming.ui.board.BoardViewModel
import com.example.swimming.ui.board.BoardViewModelFactory
import com.example.swimming.ui.user.UserViewModel
import com.example.swimming.ui.user.UserViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MyApplication() : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))

        bind() from singleton { UserDataSource() }
        bind() from singleton { UserRepository(instance()) }
        bind() from provider { UserViewModel(instance(), instance()) }
        bind() from provider { UserViewModelFactory(instance()) }

        bind() from singleton { BoardDataSource() }
        bind() from singleton { BoardRepository(instance()) }
        bind() from provider { BoardViewModel(instance(), instance()) }
        bind() from provider { BoardViewModelFactory(instance()) }
    }
}