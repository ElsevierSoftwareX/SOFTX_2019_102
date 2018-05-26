from enum import Enum


class ExtractorCartridge(Enum):
    """ This class is a enum with all the possibles extractor cartridges.
    """
    PROGRAM = 'PROGRAM'
    CSV = 'CSV'
    FITS = 'FITS'
    FASTBIT = 'FASTBIT',
    OPTIMIZED_FASTBIT = 'OPTIMIZED_FASTBIT',
    POSTGRES_RAW = 'POSTGRES_RAW'
