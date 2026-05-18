package uz.Muhammad.manual_di_app

import android.app.Application
import uz.Muhammad.manual_di_app.appContainer.AppContainer

class ManualDIApp: Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}