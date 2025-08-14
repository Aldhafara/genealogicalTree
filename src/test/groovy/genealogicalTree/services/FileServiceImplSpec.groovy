package genealogicalTree.services


import com.aldhafara.genealogicalTree.exceptions.IllegalFileFormatException
import com.aldhafara.genealogicalTree.models.gedcom.GedcomData
import com.aldhafara.genealogicalTree.models.gedcom.GedcomRecord
import com.aldhafara.genealogicalTree.services.FileServiceImpl
import com.aldhafara.genealogicalTree.services.gedcom.GedcomImportService
import com.aldhafara.genealogicalTree.services.gedcom.GedcomToJsonService
import com.aldhafara.genealogicalTree.services.gedcom.JsonGenerator
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Specification
import spock.lang.Subject

class FileServiceImplSpec extends Specification {

    GedcomToJsonService gedcomToJsonService = Mock()
    JsonGenerator jsonGenerator = Mock()
    GedcomImportService importService = Mock()
    @Subject
    FileServiceImpl fileService = new FileServiceImpl(gedcomToJsonService, jsonGenerator, importService)

    def "should analyze valid GEDCOM file and call services"() {
        given:
            def fileContent = "0 HEAD\n1 SOUR test".bytes
            def file = new MockMultipartFile("file", "test.ged", "text/plain", fileContent)

            def details = new GedcomRecord(null, "head")
            def person = new GedcomRecord("I1", "indi")
            person.addDetail("name", "John /Doe/")
            person.addDetail("sex", "M")
            def family = new GedcomRecord("F1", "fam")
            def source = new GedcomRecord("S1", "sour")
            def gedcomData = new GedcomData(details, [person], [family], [source])
            def jsonList = ['{"id":"I1"}']

        when:
            fileService.analyze(file)

        then:
            1 * gedcomToJsonService.convert(file) >> gedcomData
            1 * jsonGenerator.generateJsonList([person]) >> jsonList
            1 * importService.importFromGedcom(jsonList)
    }

    def "should throw IllegalFileFormatException when file is not GEDCOM"() {
        given:
            def file = new MockMultipartFile("file", "invalid.txt", "text/plain", "dummy".bytes)

        when:
            fileService.analyze(file)

        then:
            thrown(IllegalFileFormatException)
    }

    def "should throw RuntimeException when GEDCOM conversion fails"() {
        given:
            def file = new MockMultipartFile("file", "test.ged", "text/plain", "invalid".bytes)
            gedcomToJsonService.convert(file) >> { throw new IOException("failed") }

        when:
            fileService.analyze(file)

        then:
            def e = thrown(RuntimeException)
            e.message.contains("Failed to process GEDCOM file")
    }
}
