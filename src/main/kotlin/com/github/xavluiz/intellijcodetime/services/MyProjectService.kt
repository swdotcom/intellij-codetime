package com.github.xavluiz.intellijcodetime.services

import com.github.xavluiz.intellijcodetime.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
