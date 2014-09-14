with open('greedy.zip', 'rb') as zipFile, open('greedy.dat', 'ab+') as file:
	file.write("------WebKitFormBoundaryUjAnyaOeGq83DPKC\r\nContent-Disposition: form-data; name=\"file\"; filename=\"./greedy.zip\"\r\nContent-Type: application/octet-stream\r\nMedia-Type: application/octet-stream\r\n\r\n")
	bytes = zipFile.read()
	file.write(bytes)
	file.write("\r\n------WebKitFormBoundaryUjAnyaOeGq83DPKC--")