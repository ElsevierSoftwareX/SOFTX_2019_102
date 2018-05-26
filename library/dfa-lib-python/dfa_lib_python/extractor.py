from .ProvenanceObject import ProvenanceObject
from .extractor_cartridge import ExtractorCartridge
from .method_type import MethodType


class Extractor(ProvenanceObject):
    
    def __init__(self, tag, set_tag, extractor_cartridge, method_type):
        ProvenanceObject.__init__(self, tag)

    def add_cartridge(self, cartridge):
        assert isinstance(cartridge, ExtractorCartridge), \
            "The parameter must be a extractor cartridge."

    def add_extension(self, extension):
        assert isinstance(extension, ExtractorExtension), \
            "The parameter must be a extractor extension."
