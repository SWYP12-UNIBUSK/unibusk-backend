package team.unibusk.backend.domain.performanceLocation.domain;

import lombok.Getter;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.InvalidMapBoundsException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.NullMapBoundsException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.OutOfRangeLatitudeException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.OutOfRangeLongitudeException;

@Getter
public class MapBounds {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    private final double north;
    private final double south;
    private final double east;
    private final double west;

    public MapBounds(Double north, Double south, Double east, Double west) {
        validateNotNull(north, south, east, west);
        validateLatitudeRange(north, south);
        validateLongitudeRange(east, west);
        validateOrder(north, south, east, west);

        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    private void validateNotNull(Double north, Double south, Double east, Double west) {
        if (north == null || south == null || east == null || west == null) {
            throw new NullMapBoundsException();
        }
    }

    private void validateLatitudeRange(double north, double south) {
        if (isValidLatitude(north) || isValidLatitude(south)) {
            throw new OutOfRangeLatitudeException();
        }
    }

    private void validateLongitudeRange(double east, double west) {
        if (isValidLongitude(east) || isValidLongitude(west)) {
            throw new OutOfRangeLongitudeException();
        }
    }

    private void validateOrder(double north, double south, double east, double west) {
        if (north <= south) {
            throw new InvalidMapBoundsException();
        }
        if (east <= west) {
            throw new InvalidMapBoundsException();
        }
    }

    private boolean isValidLatitude(double latitude) {
        return !(latitude >= MIN_LATITUDE) || !(latitude <= MAX_LATITUDE);
    }

    private boolean isValidLongitude(double longitude) {
        return !(longitude >= MIN_LONGITUDE) || !(longitude <= MAX_LONGITUDE);
    }

}
