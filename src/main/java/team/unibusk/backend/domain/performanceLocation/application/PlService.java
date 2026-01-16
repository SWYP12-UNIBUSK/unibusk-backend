package team.unibusk.backend.domain.performanceLocation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PlIdServiceRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PlSearchServiceRequset;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PlIdResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PlListResponse;
import team.unibusk.backend.domain.performanceLocation.domain.Pl;
import team.unibusk.backend.domain.performanceLocation.domain.PlRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlService {

    private final PlRepository plRepository;

    //id로 공연 장소 검색
    public PlIdResponse getPlbyId(PlIdServiceRequest serviceRequest){

        Pl pl =  plRepository.findWithImagesById(serviceRequest.id());

        return PlIdResponse.from(pl);
    }

    //공연 장소 전부 조회
    public Page<PlListResponse> getAllPl(Pageable pageable){
        Page<Pl> plPage = plRepository.findAll(pageable);

        return plPage.map(PlListResponse::from);
    }

    //키워드로 공연 장소 조회
    public  Page<PlListResponse> searchPlByKeyword(
            PlSearchServiceRequset serviceRequset,
            Pageable pageable
    ){
        Page<Pl> plPage = plRepository.findByKeyword(serviceRequset.keyword(), pageable);

        return plPage.map(PlListResponse::from);
    }

}
