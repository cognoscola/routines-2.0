package com.gorillamoa.routines.core.constants

 class DataLayerConstant{

     companion object {
         /**
          * Path to show notifications on both devices
          */
         const val WAKE_UP_PATH = "/wakeup"
         const val TASK_PATH = "/task"
         const val SLEEP_PATH  = "/sleep"

         //This is for synchronizing databases from PHONE -> WEAR
         //We'll pass ALL DB items in one call for now to keep it simple
         const val DATA_TASK_WEAR_UPDATE_PATH = "/data/task/wear/update"
         const val DATA_TASK_WEAR_INSERT_PATH = "/data/task/wear/insert"
         const val DATA_TASK_WEAR_DELETE_PATH = "/data/task/wear/delete"

         //WEAR -> PHONE
         //When the user makes a change in the wear, it should reflect on phone
         const val DATA_TASK_MOBILE_INSERT_PATH = "/data/task/mobile/insert"
         const val DATA_TASK_MOBILE_DELETE_PATH = "/data/task/mobile/delete"
         const val DATA_TASK_MOBILE_UPDATE_PATH = "/data/task/mobile/update"

         //synchronize the day's progress! from Phone -> Wear
         const val PROGRESS_PATH = "/data/progress/"


         //This is for synchronizing user settings (stubborn notifications)
         //We'll pass ALL settings on every call
         const val DATA_SETTINGS = "/data/settings"

         /**
          * Field to contain information about tasks
          * Used by, Wake, Task and Sleep notifications
          */
         const val KEY_TASK_DATA = "task_data"
         const val KEY_TIME = "data.time"

         /**
          * Fields to get fields for the day's progress
          */
         const val KEY_PROGRESS_ORDER = "progress.order"
         const val KEY_PROGRESS_UNCOMPLETED = "progress.uncompleted"
         const val KEY_PROGRESS_COMPLETED = "progress.completed"
         const val KEY_PROGRESS_ACTIVE = "progress.isActive"

     }

}