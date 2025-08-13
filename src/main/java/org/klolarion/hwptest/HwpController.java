package org.klolarion.hwptest;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.docinfo.CharShape;
import kr.dogfoot.hwplib.object.docinfo.ParaShape;
import kr.dogfoot.hwplib.tool.blankfilemaker.BlankFileMaker;
import kr.dogfoot.hwplib.writer.HWPWriter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("/hwp")
public class HwpController {

    @RequestMapping(value = {"/down"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<InputStreamResource> downloadHwpFile() {
        try {
            //HWPFile hwpFile = new HWPFile();
            HWPFile hwpFile = BlankFileMaker.make(); // 빈 한글파일 생성


            Section s = hwpFile.getBodyText().getSectionList().get(0); // 첫 번째 섹션(인덱스 0)을 가져옴
            Paragraph firstParagraph = s.getParagraph(0); // 방금 얻은 섹션의 "문단(Paragraph)" 중 첫 번째 문단(인덱스 0)을 가져옴
            // firstParagraph.createText(); // 뭔가 에러 발생...
            firstParagraph.getText().addString("안녕하세요. 저는 한글파일 입니다."); // 텍스트 추가

            int csId = hwpFile.getDocInfo().getCharShapeList().size();
            CharShape cs = hwpFile.getDocInfo().addNewCharShape();
            cs.setBaseSize(1600);                        // 16pt = 1600 (1/100pt)
            cs.getProperty().setBold(true);              // 굵게
            cs.getCharColor().setValue(0x00000000);      // 글자색: 검정
            cs.getShadeColor().setValue(0x00FFFFFF);     // 배경색: 흰색
            // CharShape조건을 설정하면 글자색과 배경색이 기본으로 검정이 되는듯하여 배경색을 흰색으로 설정해줘야함.

            // 문단에 “0부터 전부 csId 적용”
            firstParagraph.createCharShape();
            firstParagraph.getCharShape().addParaCharShape(0, csId);

            firstParagraph.deleteLineSeg(); // 줄 배치 캐시 클리어


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HWPWriter.toStream(hwpFile, byteArrayOutputStream);
            byte[] hwpBytes = byteArrayOutputStream.toByteArray(); // 바이트배열로 변환

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(hwpBytes);
            InputStreamResource inputStreamResource = new InputStreamResource(byteArrayInputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hwpFile.hwp"); // 헤더 추가

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(hwpBytes.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(inputStreamResource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
