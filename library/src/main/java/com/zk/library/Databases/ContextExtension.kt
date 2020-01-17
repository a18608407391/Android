package org.cs.tec.library.Databases

import android.content.Context
import org.cs.tec.library.Base.Utils.context


val Context.database:LibraryDatabases
get()= LibraryDatabases.getIncetance(context = context)!!