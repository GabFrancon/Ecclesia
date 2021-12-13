from cx_Freeze import setup, Executable

setup(name = "BenenovaScraper", version = "1", description = "Scraper for Benenova.com", executables = [Executable("BenenovaScraper.py")])