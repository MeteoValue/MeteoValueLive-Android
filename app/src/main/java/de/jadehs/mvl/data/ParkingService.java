package de.jadehs.mvl.data;

import java.util.NoSuchElementException;

import de.jadehs.mvl.data.models.parking.Parking;
import de.jadehs.mvl.data.models.parking.ParkingCurrOccupancy;
import de.jadehs.mvl.data.models.parking.ParkingDailyStats;
import io.reactivex.rxjava3.core.Single;

public interface ParkingService {

    /**
     * Single does resolve to an array of all rest stops and their occupied parking spots information
     *
     * @return a list of {@link ParkingCurrOccupancy} objects
     */
    Single<ParkingCurrOccupancy[]> getAllOccupancies();

    /**
     * Single does resolve to the searched rest stop and his occupied parking spots information
     * <p>
     * could resolve with an error of type {@link NoSuchElementException}
     *
     * @return a list of {@link ParkingCurrOccupancy} objects
     */
    default Single<ParkingCurrOccupancy> getOccupancy(String id) {
        return getAllOccupancies().map(occupancy -> {

            for (ParkingCurrOccupancy parkingDailyStat : occupancy) {
                if (parkingDailyStat.getId().equals(id)) {
                    return parkingDailyStat;
                }
            }
            throw new NoSuchElementException("couldn't find a curr occupancy of the given parking spot");

        });
    }

    /**
     * Single does resolve to an array of all rest stops and their occupied parking estimation and history
     *
     * @return a list of {@link ParkingCurrOccupancy} objects
     */
    Single<ParkingDailyStats[]> getAllParkingDailyStats();

    /**
     * Single does resolve to the requested rest stop and his occupied parking estimation and history
     * <p>
     * could resolve with an error of type {@link NoSuchElementException}
     *
     * @return a list of {@link ParkingDailyStats} objects
     */
    default Single<ParkingDailyStats> getParkingDailyStat(String id) {
        return getAllParkingDailyStats().map(parkingDailyStats -> {

            for (ParkingDailyStats parkingDailyStat : parkingDailyStats) {
                if (parkingDailyStat.getId().equals(id)) {
                    return parkingDailyStat;
                }
            }
            throw new NoSuchElementException("couldn't find a daily stat of the given parking spot");

        });
    }

    /**
     * Single does resolve to an array of all rest stops
     *
     * @return an array of {@link Parking} objects
     */
    Single<Parking[]> getAllParking();

    default Single<Parking> getParking(String id) {
        return getAllParking().map(parkings -> {

            for (Parking parking : parkings) {
                if (parking.getId().equals(id)) {
                    return parking;
                }
            }
            throw new NoSuchElementException("couldn't find a parking of the given parking spot");

        });
    }
}
