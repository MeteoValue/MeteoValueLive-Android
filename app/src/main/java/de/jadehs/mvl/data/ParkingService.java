package de.jadehs.mvl.data;

import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.models.parking.ParkingDailyStats;
import io.reactivex.rxjava3.core.Single;

public interface ParkingService {

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
