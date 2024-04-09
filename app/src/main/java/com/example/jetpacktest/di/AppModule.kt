package com.example.jetpacktest.di

import com.example.jetpacktest.common.Constants
import com.example.jetpacktest.data.remote.BdlApi
import com.example.jetpacktest.data.remote.RestApi
import com.example.jetpacktest.data.remote.SportsDataApi
import com.example.jetpacktest.data.repository.GamesRepositoryImpl
import com.example.jetpacktest.data.repository.HeadshotRepositoryImpl
import com.example.jetpacktest.data.repository.TeamRepositoryImpl
import com.example.jetpacktest.domain.repository.GamesRepository
import com.example.jetpacktest.domain.repository.HeadshotRepository
import com.example.jetpacktest.domain.repository.TeamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class) // All dependencies live as long as app
object AppModule {
    @Provides
    @Singleton // Only one instance
    fun provideBdlApi(): BdlApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BDL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BdlApi::class.java)
    }
    @Provides
    @Singleton
    fun provideSportsDataApi(): SportsDataApi {
        return Retrofit.Builder()
            .baseUrl(Constants.SPORTS_DATA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SportsDataApi::class.java)
    }
    @Provides
    @Singleton
    fun provideRestApi(): RestApi {
        return Retrofit.Builder()
            .baseUrl(Constants.REST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGamesRepository(api: BdlApi): GamesRepository {
        return GamesRepositoryImpl(api = api) // Return Impl. even though return value is interface
    }
    @Provides
    @Singleton
    fun provideHeadshotRepository(api: SportsDataApi): HeadshotRepository {
        return HeadshotRepositoryImpl(api = api)
    }
    @Provides
    @Singleton
    fun provideTeamRepository(api: SportsDataApi): TeamRepository {
        return TeamRepositoryImpl(api = api)
    }


}