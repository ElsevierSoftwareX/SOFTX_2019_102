from .ProvenanceObject import ProvenanceObject


class File(ProvenanceObject):
    def __init__(self, path, name):
        ProvenanceObject.__init__(self, name)
        self._path = path
        self._name = name
