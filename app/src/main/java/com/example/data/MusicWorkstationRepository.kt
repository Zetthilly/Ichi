package com.example.data

import kotlinx.coroutines.flow.Flow

class MusicWorkstationRepository(private val workstationDao: WorkstationDao) {

    val allSessions: Flow<List<ProjectSession>> = workstationDao.getAllSessionsFlow()
    val allLicks: Flow<List<GuitarLick>> = workstationDao.getAllLicksFlow()

    fun getLicksByGenre(genre: String): Flow<List<GuitarLick>> = workstationDao.getLicksByGenre(genre)

    suspend fun getSessionById(id: Int): ProjectSession? = workstationDao.getSessionById(id)

    suspend fun insertSession(session: ProjectSession): Long = workstationDao.insertSession(session)

    suspend fun updateSession(session: ProjectSession) = workstationDao.updateSession(session)

    suspend fun deleteSessionById(id: Int) = workstationDao.deleteSessionById(id)

    suspend fun insertLick(lick: GuitarLick): Long = workstationDao.insertLick(lick)

    suspend fun updateLickFavorite(id: Int, isFavorite: Boolean) = workstationDao.updateLickFavorite(id, isFavorite)

    suspend fun deleteLickById(id: Int) = workstationDao.deleteLickById(id)
}
