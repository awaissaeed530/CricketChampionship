package io.awais.cricket_championship.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.awais.cricket_championship.engine.MatchEngine
import io.awais.cricket_championship.engine.MatchConditions
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMatchConditions(): MatchConditions {
        return MatchConditions()
    }

    @Provides
    @Singleton
    fun provideMatchEngine(matchConditions: MatchConditions): MatchEngine {
        return MatchEngine(matchConditions)
    }
}
