with open('upload.zip', 'rb') as zipFile, open('upload.dat', 'wb+') as file:
	file.write("------WebKitFormBoundaryUjAnyaOeGq83DPKC\r\nContent-Disposition: form-data; name=\"file\"; filename=\"./upload.zip\"\r\nContent-Type: application/octet-stream\r\nMedia-Type: application/octet-stream\r\n\r\n")
	bytes = zipFile.read()
	file.write(bytes)
	file.write("\r\n------WebKitFormBoundaryUjAnyaOeGq83DPKC--")