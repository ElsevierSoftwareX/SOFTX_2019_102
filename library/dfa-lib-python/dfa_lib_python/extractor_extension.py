from enum import Enum


class ExtractorExtension(Enum):
    PROGRAM = 'PROGRAM'
    CSV = 'CSV'
    FITS = 'FITS'
    FASTBIT = 'FASTBIT',
    OPTIMIZED_FASTBIT = 'OPTIMIZED_FASTBIT',
    POSTGRES_RAW = 'POSTGRES_RAW'
