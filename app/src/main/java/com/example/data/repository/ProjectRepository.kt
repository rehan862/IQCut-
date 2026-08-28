package com.example.data.repository

import com.example.data.db.ProjectDao
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun getProject(id: String): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun saveProject(project: ProjectEntity) = projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)

    suspend fun deleteProject(id: String) = projectDao.deleteProjectById(id)

    suspend fun renameProject(id: String, newName: String) = projectDao.renameProject(id, newName)

    suspend fun getProjectCount(): Int = projectDao.getProjectCount()
}
