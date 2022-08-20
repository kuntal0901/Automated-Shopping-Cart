import bs4
import requests
from selenium import webdriver
import os
import time
from selenium.webdriver.common.keys import Keys

# fruits=["apple","banana","custard_apple","fig","grape","jackfruit","mango","orange","papaya","pear","pineapple","strawberry","watermelon","muskmelon","Mosambi","Pomegranate","DragonFruit","Kiwi","Guava","Sapodilla"]
fruits=["Kiwi","Guava","Sapodilla"]
for i in range(len(fruits)):
    temp=fruits[i]
    temp=temp.replace('_',' ')
    temp=temp.capitalize()
    fruits[i]=temp
first=0
#creating a directory to save images
driver = webdriver.Chrome('C:\chromedriver.exe')
driver.get('https://www.google.ca/imghp?hl=en&tab=ri&authuser=0&ogbl')
total_errors=0
for i in fruits:
    desired_len=1000
    first+=1
    folder_name = '.\\dataset\\train\\fruits\\'+i
    if not os.path.isdir(folder_name):
        os.makedirs(folder_name)
    else:
        tmp=os.listdir(folder_name)
        desired_len-=len(tmp)
        
    def download_image(url, folder_name, num):
        # write image to file
        reponse = requests.get(url)
        if reponse.status_code==200:
            with open(os.path.join(folder_name,str(num)+"_z.jpg"), 'wb') as file:
                file.write(reponse.content)
    if(first==1):
        box = driver.find_element('xpath','/html/body/div[1]/div[3]/form/div[1]/div[1]/div[1]/div/div[2]/input')
        box.send_keys(i+' fruit')
        box.send_keys(Keys.ENTER)
    else:
        box = driver.find_element('xpath','//*[@id="REsRA"]')
        for blah in range(10):
            box.send_keys(Keys.CONTROL+Keys.BACK_SPACE)
        box.send_keys(i+' fruit')
        box.send_keys(Keys.ENTER)
        

    
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

    #Scrolling all the way up
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
            # print("For image number: ",i,"Found previewImageXpath")
            previewImageElement = driver.find_element('xpath',previewImageXPath)
            previewImageURL = previewImageElement.get_attribute("src")
            driver.find_element('xpath',xPath).click()
        except:
            print("For image number: ",i,"Could Not Find previewImageXpath")
            total_errors+=1
            continue

        timeStarted = time.time()
        # print("For image number: ",i,"Found ImageURL by clicking on that retriving src attribute")
        while True:
            try:              
#               #//*[@id="Sva75c"]/div/div/div[3]/div[2]/c-wiz/div/div[1]/div[1]/div[3]/div/a/img
                #//*[@id="Sva75c"]/div/div/div[3]/div[2]/c-wiz/div/div[1]/div[1]/div[3]/div/a/img
                imageElement = driver.find_element('xpath',"""//*[@id="Sva75c"]/div/div/div[3]/div[2]/c-wiz/div/div[1]/div[1]/div[3]/div/a/img""")
                imageURL= imageElement.get_attribute('src')
                
            
                if imageURL != previewImageURL:
                    #print("actual URL", imageURL)
                    # print("For image number: ",i," previewImageXpath and Image URL donot match")
                    break

                else:
                    # making a timeout if the full res image can't be loaded
                    currentTime = time.time()

                    if currentTime - timeStarted > 10:
        #                     print("Timeout! Will download a lower resolution image and move onto the next one")
                        # print("For image number: ",i,"Timeout")                    
                        break
            except:
                # print("For image number: ",i,"Could not Find ImageXpath")
                break
        


        try:
            download_image(imageURL, folder_name, i)
#             print("Downloaded element %s out of %s total. URL: %s" % (i, len_containers + 1, imageURL))
            print("Downloaded element %s out of %s total."%(i,len_containers+1))
            countersss+=1
            if(countersss>=desired_len):
                print("Exceeded Length Doubt this occurs")
                break
        except:
            total_errors+=1
            print("Couldn't download an image %s, continuing downloading the next one"%(i))
    print("For class ",i," The number of failures are ",total_errors)

        