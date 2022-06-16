package de.jadehs.mvl.data.parking.remote;

import de.jadehs.mvl.data.parking.models.Parking;
import de.jadehs.mvl.data.parking.models.ParkingCurrOccupancy;
import de.jadehs.mvl.data.parking.models.ParkingDailyStats;
import io.reactivex.rxjava3.core.Single;

public interface ParkingManager {

    /**
     * Single does resolve to an array of all rest stops and their occupied parking spots information
     * @return a list of {@link ParkingCurrOccupancy} objects
     */
    Single<ParkingCurrOccupancy[]> getAllOccupancies();

    /**
     * Single does resolve to an array of all rest stops and their occupied parking estimation and history
     * @return a list of {@link ParkingCurrOccupancy} objects
     */
    Single<ParkingDailyStats[]> getAllParkingDailyStats();

    /**
     * Single does resolve to an array of all rest stops
     * @return an array of {@link Parking} objects
     */
    Single<Parking[]> getAllParking();
}
