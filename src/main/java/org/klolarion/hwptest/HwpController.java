package org.klolarion.hwptest;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.tool.blankfilemaker.BlankFileMaker;
import kr.dogfoot.hwplib.writer.HWPWriter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("/hwp")
public class HwpController {

    @RequestMapping(value = {"/", "/download"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<InputStreamResource> downloadHwpFile() {
        try {
            //HWPFile hwpFile = new HWPFile();
            HWPFile hwpFile = BlankFileMaker.make(); // 빈 한글파일 생성


            Section s = hwpFile.getBodyText().getSectionList().get(0); //
            Paragraph firstParagraph = s.getParagraph(0); //
            firstParagraph.getText().addString("안녕하세요."); // 텍스트 추가

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
