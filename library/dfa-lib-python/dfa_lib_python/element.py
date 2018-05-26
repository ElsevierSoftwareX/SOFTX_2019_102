class Element(object):
    """
    This class defines a dataset element.

    Attributes:
        - values (list): Element values.
    """

    def __init__(self, values):
        self.values = values

    @property
    def values(self):
        """Get or set the element values."""
        separator = ";"
        result = [str(x) for x in self._values]
        return separator.join(result)

    @values.setter
    def values(self, values):
        assert isinstance(values, list), \
            "The values must be in a list."
        self._values = [str(x) for x in values]
