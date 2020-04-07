:: Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
:: reserved. Use of this source code is governed by a BSD-style license
:: that can be found in the LICENSE file.

:: Compile the test program.
javac -cp "./;.//*" ./tests/detailed/*.java ./tests/detailed/dialog/*.java ./tests/detailed/handler/*.java ./tests/detailed/ui/*.java ./tests/simple/*.java

:: Create the test JAR file.
jar -cf jcef-tests.jar tests/detailed/*.class tests/detailed/dialog/*.class tests/detailed/handler/*.class tests/detailed/ui/*.class tests/simple/*.java

