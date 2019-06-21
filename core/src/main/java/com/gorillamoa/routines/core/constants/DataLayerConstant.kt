package com.gorillamoa.routines.core.constants

 class DataLayerConstant{

     companion object {
         /**
          * Path to show notifications on both devices
          */
         const val WAKE_UP_PATH = "/wakeup"
         const val TASK_PATH = "/task"
         const val SLEEP_PATH  = "/sleep"

         /**
          * Field to contain information about tasks
          * Used by, Wake, Task and Sleep notifications
          */
         const val KEY_TASK_DATA = "task_data"
         const val KEY_TASK_ID = "Task.data.id"
         const val KEY_TIME = "data.time"

     }

}