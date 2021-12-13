import scrapy





class HorizonEuropeSpider(scrapy.Spider):

    name = 'horizon_Europe'

    allowed_domains = ['www.iledefrance-europe.eu/opportunites/detail-actualites-opportunites/article/vous-souhaitez-coordonner-un-projet-collaboratif-horizon-europe/']

    start_urls = ['http://www.iledefrance-europe.eu/opportunites/detail-actualites-opportunites/article/vous-souhaitez-coordonner-un-projet-collaboratif-horizon-europe//']



    def parse(self, response):
      from typing import Dict, Any
      
      titresH1=response.css('h1::text').extract()
      titresH2=response.css('h2::text').extract()
      titresH3=response.css('h3::text').extract()
      
      return titresH1, titresH2, titresH3
      
      #for item in zip(titresH1,titresH2,titresH3):
      #    scraped_info: Dict[str, Any] = {
      #        'title' : item[0],
      #        'vote' : item[1],
      #        'created_at' : item[2]
      #    }
      
      #    return scraped_info