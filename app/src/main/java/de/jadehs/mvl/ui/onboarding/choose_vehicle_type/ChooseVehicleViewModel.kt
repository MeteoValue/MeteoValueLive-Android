package de.jadehs.mvl.ui.onboarding.choose_vehicle_type

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChooseVehicleViewModel : ViewModel() {

    private val _vehicleType = MutableLiveData<Int>()

    val vehicleType: LiveData<Int>
        get() = _vehicleType


    fun setVehicleType(number: Int){
        _vehicleType.value = number
    }

}