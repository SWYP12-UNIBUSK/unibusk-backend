package team.unibusk.backend.domain.performanceLocation.domain;

import org.junit.jupiter.api.Test;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.InvalidMapBoundsException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.NullMapBoundsException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.OutOfRangeLatitudeException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.OutOfRangeLongitudeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapBoundsTest {

    @Test
    void 유효한_범위의_좌표로_MapBounds를_생성한다() {
        var bounds = new MapBounds(37.6, 37.5, 127.0, 126.9);

        assertThat(bounds.getNorth()).isEqualTo(37.6);
        assertThat(bounds.getSouth()).isEqualTo(37.5);
        assertThat(bounds.getEast()).isEqualTo(127.0);
        assertThat(bounds.getWest()).isEqualTo(126.9);
    }

    @Test
    void north가_null이면_NullMapBoundsException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(null, 37.5, 127.0, 126.9))
                .isInstanceOf(NullMapBoundsException.class);
    }

    @Test
    void south가_null이면_NullMapBoundsException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.6, null, 127.0, 126.9))
                .isInstanceOf(NullMapBoundsException.class);
    }

    @Test
    void east가_null이면_NullMapBoundsException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.6, 37.5, null, 126.9))
                .isInstanceOf(NullMapBoundsException.class);
    }

    @Test
    void west가_null이면_NullMapBoundsException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.6, 37.5, 127.0, null))
                .isInstanceOf(NullMapBoundsException.class);
    }

    @Test
    void 위도가_90을_초과하면_OutOfRangeLatitudeException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(91.0, 37.5, 127.0, 126.9))
                .isInstanceOf(OutOfRangeLatitudeException.class);
    }

    @Test
    void 위도가_마이너스_90_미만이면_OutOfRangeLatitudeException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.6, -91.0, 127.0, 126.9))
                .isInstanceOf(OutOfRangeLatitudeException.class);
    }

    @Test
    void 경도가_180을_초과하면_OutOfRangeLongitudeException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.6, 37.5, 181.0, 126.9))
                .isInstanceOf(OutOfRangeLongitudeException.class);
    }

    @Test
    void 경도가_마이너스_180_미만이면_OutOfRangeLongitudeException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.6, 37.5, 127.0, -181.0))
                .isInstanceOf(OutOfRangeLongitudeException.class);
    }

    @Test
    void 위도_경계값은_허용된다() {
        var bounds = new MapBounds(90.0, -90.0, 127.0, 126.9);

        assertThat(bounds.getNorth()).isEqualTo(90.0);
        assertThat(bounds.getSouth()).isEqualTo(-90.0);
    }

    @Test
    void 경도_경계값은_허용된다() {
        var bounds = new MapBounds(37.6, 37.5, 180.0, -180.0);

        assertThat(bounds.getEast()).isEqualTo(180.0);
        assertThat(bounds.getWest()).isEqualTo(-180.0);
    }

    @Test
    void north가_south보다_작거나_같으면_InvalidMapBoundsException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.5, 37.5, 127.0, 126.9))
                .isInstanceOf(InvalidMapBoundsException.class);
    }

    @Test
    void east가_west보다_작거나_같으면_InvalidMapBoundsException이_발생한다() {
        assertThatThrownBy(() -> new MapBounds(37.6, 37.5, 126.9, 126.9))
                .isInstanceOf(InvalidMapBoundsException.class);
    }

}
