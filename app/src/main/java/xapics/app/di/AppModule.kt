package xapics.app.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xapics.app.data.auth.AuthRepository
import xapics.app.data.auth.AuthRepositoryImpl
import xapics.app.data.auth.backup.AndroidDownloader
import xapics.app.data.auth.backup.Downloader
import xapics.app.data.EncryptedSharedPrefs
import xapics.app.data.EncryptedSharedPrefsImpl
import xapics.app.data.PicsApi
import xapics.app.data.PicsApi.Companion.BASE_URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePicsApi(): PicsApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PicsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPref(impl: EncryptedSharedPrefsImpl): EncryptedSharedPrefs {
        return impl
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: PicsApi, cryptoPrefs: EncryptedSharedPrefs): AuthRepository {
        return AuthRepositoryImpl(api, cryptoPrefs)
    }

    @Provides
    @Singleton
    fun provideAndroidDownloader(prefs: SharedPreferences): Downloader {
        return AndroidDownloader(prefs)
    }
}