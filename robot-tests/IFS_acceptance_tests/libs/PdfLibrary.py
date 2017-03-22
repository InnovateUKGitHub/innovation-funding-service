from StringIO import StringIO
from pdfminer.pdfinterp import PDFResourceManager, PDFPageInterpreter
from pdfminer.converter import TextConverter
from pdfminer.layout import LAParams
from pdfminer.pdfpage import PDFPage


def convert_pdf_to_text(fname, pages=None):
    if not pages:
        pagenums = set()
    else:
        pagenums = set(pages)

    output = StringIO()
    manager = PDFResourceManager()
    converter = TextConverter(manager, output, laparams=LAParams())
    interpreter = PDFPageInterpreter(manager, converter)

    infile = file(fname, 'rb')
    for page in PDFPage.get_pages(infile, pagenums):
        interpreter.process_page(page)
    infile.close()
    converter.close()
    text = output.getvalue()
    output.close()
    unicode_text = unicode(text, "utf-8")
    tidy_text = "".join(unicode_text.split())
    return tidy_text



def pdf_should_contain(filename, user_text):
    text_from_pdf = convert_pdf_to_text(filename)
    print text_from_pdf
    if user_text in text_from_pdf:
        return True  # could return anything though, really
    else:
        raise AssertionError("Text not found " + user_text)