import yaml
import bs4
import requests
from selenium import webdriver
import os
import time
from selenium.webdriver.common.keys import Keys
import pathlib
fp=open("Classes.yaml")
d1=yaml.safe_load(fp)
# print(d1)
def download_images_name(name,class_bel):
    total_errors=0
    driver = webdriver.Chrome('C:\chromedriver.exe')
    driver.get('https://www.google.co.in/imghp?hl=en&authuser=0&ogbl')
    box = driver.find_element('xpath','/html/body/div[1]/div[3]/form/div[1]/div[1]/div[1]/div/div[2]/input')
    if(name!="Brown Coconut"):
        box.send_keys(name+" "+class_bel)
    else:
        box.send_keys(name)
    box.send_keys(Keys.ENTER)
    folder_name =".\\NewDataset\\"+name
    if not os.path.isdir(folder_name):
        os.makedirs(folder_name)
    else:
        os.rmdir(folder_name)
    def download_image(url, folder_name, num):
        reponse = requests.get(url)
        if reponse.status_code==200:
            with open(os.path.join(folder_name,str(num)+"_z.jpeg"), 'wb') as file:
                file.write(reponse.content)
    last_height = driver.execute_script('return document.body.scrollHeight')
    counter=0
    while counter<12:
        driver.execute_script('window.scrollTo(0,document.body.scrollHeight)')
        time.sleep(2)
        new_height = driver.execute_script('return document.body.scrollHeight')
        try:
            driver.find_element('xpath','//*[@id="islmp"]/div/div/div/div[1]/div[2]/div[2]/input').click()
            counter+=1
            time.sleep(2)
        except:
            
            pass
        if new_height == last_height:
            break
        last_height = new_height
    i=name
    driver.execute_script("window.scrollTo(0, 0);")
    page_html = driver.page_source
    pageSoup = bs4.BeautifulSoup(page_html, 'html.parser')
    containers = pageSoup.findAll('div', {'class':"isv-r PNCib MSM1fd BUooTd"} )
    len_containers = len(containers)
    print("len_containers for ",i," is ",len_containers)
    countersss=0
    for i in range(1, len_containers+1):        
        xPath = """//*[@id="islrg"]/div[1]/div[%s]"""%(i)
        print("For image number: ",i,"Just started")
        try:
            previewImageXPath = """//*[@id="islrg"]/div[1]/div[%s]/a[1]/div[1]/img"""%(i)
            previewImageElement = driver.find_element('xpath',previewImageXPath)
            previewImageURL = previewImageElement.get_attribute("src")
            driver.find_element('xpath',xPath).click()
        except:
            print("For image number: ",i,"Could Not Find previewImageXpath")
            total_errors+=1
            continue

        timeStarted = time.time()

        while True:
            try:
                imageElement = driver.find_element('xpath',"""//*[@id="Sva75c"]/div/div/div[3]/div[2]/c-wiz/div/div[1]/div[1]/div[3]/div/a/img""")
                imageURL= imageElement.get_attribute('src')

                if imageURL != previewImageURL:
                    break

                else:
                    currentTime = time.time()
                    if currentTime - timeStarted > 10:                   
                        break
            except:
                break
        try:
            download_image(imageURL, folder_name, i)
            print("Downloaded element %s out of %s total."%(i,len_containers+1))
            countersss+=1            
        except:
            total_errors+=1
            print("Couldn't download an image %s, continuing downloading the next one"%(i))
    print("For class ",i," The number of failures are ",total_errors)
    driver.close()
for i in d1:
    for j in d1[i]:
        download_images_name(j,i)
    