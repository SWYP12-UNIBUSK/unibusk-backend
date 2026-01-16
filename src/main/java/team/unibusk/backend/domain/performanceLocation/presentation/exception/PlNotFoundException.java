package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.global.exception.CustomException;

public class PlNotFoundException extends CustomException {
    public PlNotFoundException(){
        super(PlExceptionCode.PL_NOT_FOUND_EXCEPTION);
    }
}
