#!/usr/bin/python3.6
# -*- coding: UTF-8 -*-# enable debugging

def printJsonHeader():
  print("Content-Type: application/json;charset=utf-8")
  print()

def printErrorHeader(): 
  print("Status: 500 An internal error has happened while running the Python script")
  print()

def readJson():
  file = open("wiski_reference.json", "r")
  return file.read()

def runLogic():
  try:
    readJson()
    return True
  except:
    raise Exception("An internal error has happened while running the Python script")
    return False

def main():
  ok = runLogic()
  if ok == True:
    printJsonHeader()
    json = readJson()
    print(json)
  else:
    printErrorHeader()

main()
#!/usr/bin/python3.6
# -*- coding: UTF-8 -*-# enable debugging

print("Content-Type: text/html;charset=utf-8")
print()
print("[]")
