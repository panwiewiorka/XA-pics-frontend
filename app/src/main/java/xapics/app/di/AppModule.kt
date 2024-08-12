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
import xapics.app.data.db.XaDao
import xapics.app.domain.PicsRepository
import xapics.app.domain.PicsRepositoryImpl
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.auth.AuthRepositoryImpl
import xapics.app.domain.useCases.SearchPicsUseCase
import xapics.app.domain.useCases.UseCases
import xapics.app.domain.useCases.stateHistory.GetCaptionFlowUseCase
import xapics.app.domain.useCases.stateHistory.GetCaptionUseCase
import xapics.app.domain.useCases.stateHistory.GetStateSnapshotFlowUseCase
import xapics.app.domain.useCases.stateHistory.GetStateSnapshotUseCase
import xapics.app.domain.useCases.stateHistory.LoadCaptionUseCase
import xapics.app.domain.useCases.stateHistory.PopulateCaptionTableUseCase
import xapics.app.domain.useCases.stateHistory.PopulateStateSnapshotTableUseCase
import xapics.app.domain.useCases.stateHistory.SaveCaptionUseCase
import xapics.app.domain.useCases.stateHistory.UpdateStateSnapshotUseCase
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
    fun provideEncryptedSharedPref(impl: EncryptedSharedPrefsImpl): EncryptedSharedPrefs {
        return impl
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: PicsApi, dao: XaDao, cryptoPrefs: EncryptedSharedPrefs): AuthRepository {
        return AuthRepositoryImpl(api, dao, cryptoPrefs)
    }

    @Provides
    @Singleton
    fun providePicsRepository(api: PicsApi, dao: XaDao): PicsRepository {
        return PicsRepositoryImpl(api, dao)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "appdb"
    ).build().getDao()

    @Provides
    @Singleton
    fun provideUseCases(dao: XaDao, api: PicsApi): UseCases {
        return UseCases(
            populateCaptionTable = PopulateCaptionTableUseCase(dao),
            populateStateSnapshot = PopulateStateSnapshotTableUseCase(dao),
            loadCaption = LoadCaptionUseCase(dao),
            getCaptionFlow = GetCaptionFlowUseCase(dao),
            getCaption = GetCaptionUseCase(dao),
            saveCaption = SaveCaptionUseCase(dao),
            getStateSnapshot = GetStateSnapshotUseCase(dao),
            getStateSnapshotFlow = GetStateSnapshotFlowUseCase(dao),
            updateStateSnapshot = UpdateStateSnapshotUseCase(dao),
            searchPics = SearchPicsUseCase(dao, api),
        )
    }
}