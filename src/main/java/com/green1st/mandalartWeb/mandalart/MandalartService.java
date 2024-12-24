package com.green1st.mandalartWeb.mandalart;

import com.green1st.mandalartWeb.mandalart.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MandalartService {
    private final MandalartMapper mapper;
    private final CalculateColorCode calculateColorCode;

    public List<MandalartGetRes> getMandalart (MandalartGetReq p , List<MandalartGetDto> list){
        // 프로젝트 id 체크 -> 만다라트 id 부여 -> 하위 테이블 데이터 전부 출력
        if (p.getProjectId() == 0){
            return null;
        }
        // p 데이터 조회 체크
        List<MandalartGetRes> res = mapper.getMandalart(p);
        if (list == null){
            return res;
        }

        // depth = 0 일 경우 depth0 의 order_id 카운트 - colorCodes 클래스의 titleColor
        // depth = 1 일 경우 depth1의 order_id 카운트(레벨2) - colorCodes 클래스의 subTitleColor
        // depth(레벨) - order_id(하위 목표) 의 completedFg 계산해서 colorCodes 클래스 호출해서 색상 입력

        // colorcodes 클래스 객체 생성
        ColorCodes colorCodes = new ColorCodes();

        for (MandalartGetRes item : res) {
            List<MandalartGetDto> dtoList = item.getMandalartIdGetList();
            if (dtoList == null || dtoList.isEmpty()) {
                item.setColorCodes(colorCodes.getDefaultColor()); // 데이터가 없으면 기본 색상
                continue;
            }
            // 완료 항목 카운트
            int completedCount = 0;
            for (MandalartGetDto dto : dtoList) {
                if (dto.getCompletedFg() == 1) {
                    completedCount++;
                }
            }

            // 완료율 계산
            double completionRate = (double) completedCount / dtoList.size();

            // 색상 계산
            String color = CalculateColorCode.calculateColorCode(item.getDepth(), completionRate, colorCodes);
            item.setColor(color);
        }

        return res;
    }

    @Transactional
    public List<MandalartPostRes> patchMand (MandalartPostReq p){
        List<MandalartPostDto>  list = mapper.patchMand(p);
        if (list.size() == 0){
            return new ArrayList<>();
        }

        MandalartPostRes res = new MandalartPostRes();
        res.setProjectId(p.getProjectId());
        res.setMandalartIdPostList(list);

        List<MandalartPostRes> result = new ArrayList<>();
        result.add(res);

        return result;
    }
    private String calculateColorCode(MandalartGetRes item, ColorCodes colorCodes) {
        int completedCount = item.getCompletedCount();
        // completedCount에 따라 색상을 결정 (예시: 0 ~ 8까지의 단계)
//        if (item.getDepth() == 0) {
            // depth 0인 경우: colorCodes.titleColor 사용
            return getColorForLevel(completedCount, colorCodes.getTitleColor());
//        } else if (item.getDepth() == 1) {
            // depth 1인 경우: colorCodes.subTitleColor 사용
//            return getColorForLevel(completedCount, colorCodes.getSubTitleColor());
//        }
//        return colorCodes.getDefaultColor();  // 기본 색상
    }
    private String getColorForLevel(int completedCount, List<String> colorRange) {
        // completedCount에 따라 색상 결정 (0~8)
        if (completedCount == 0) {
            return colorRange.get(0);  // 가장 어두운 색상
        } else if (completedCount == 1) {
            return colorRange.get(1);
        } else if (completedCount == 2) {
            return colorRange.get(2);
        } else if (completedCount == 3) {
            return colorRange.get(3);
        } else if (completedCount == 4) {
            return colorRange.get(4);
        } else if (completedCount == 5) {
            return colorRange.get(5);
        } else if (completedCount == 6) {
            return colorRange.get(6);
        } else if (completedCount == 7) {
            return colorRange.get(7);
        } else {
            return colorRange.get(8);  // 가장 연한 색상
        }
    }

}
