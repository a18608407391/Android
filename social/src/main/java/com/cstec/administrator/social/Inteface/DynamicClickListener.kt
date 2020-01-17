package com.cstec.administrator.social.Inteface

import com.cstec.administrator.social.Entity.GridClickEntity
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Entity.DynamicsSimple
import org.cs.tec.library.binding.command.BindingCommand


interface DynamicClickListener {

    fun LikeClick(entiy: DynamicsCategoryEntity.Dynamics)

    fun storeClick(view: DynamicsCategoryEntity.Dynamics)

    fun yelpClick(view: DynamicsCategoryEntity.Dynamics)

    fun retransClick(view: DynamicsCategoryEntity.Dynamics)

    fun avatarClick(view: DynamicsCategoryEntity.Dynamics)

    fun FocusClick(view: DynamicsCategoryEntity.Dynamics)

    fun bindingCommand() :BindingCommand<GridClickEntity>

    fun spanclick():BindingCommand<DynamicsSimple>

    fun deleteClick(view: DynamicsCategoryEntity.Dynamics)

}