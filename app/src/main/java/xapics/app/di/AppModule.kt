package xapics.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xapics.app.data.EncryptedSharedPrefs
import xapics.app.data.EncryptedSharedPrefsImpl
import xapics.app.data.PicsApi
import xapics.app.data.PicsApi.Companion.BASE_URL
import xapics.app.data.db.AppDatabase
import xapics.app.domain.PicsRepository
import xapics.app.domain.PicsRepositoryImpl
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.auth.AuthRepositoryImpl
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

//    @Provides
//    @Singleton
//    fun provideSharedPref(app: Application): SharedPreferences {
//        return app.getSharedPreferences("prefs", MODE_PRIVATE)
//    }

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
    fun providePicsRepository(api: PicsApi): PicsRepository {
        return PicsRepositoryImpl(api)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "appdb"
    ).build().getDao()
}