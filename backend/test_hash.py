from hashlib import sha256
from os import urandom
from binascii import hexlify

a = urandom(32)

print str(hexlify(str(a)))

b = sha256(a)

print str(hexlify(str(b)))
