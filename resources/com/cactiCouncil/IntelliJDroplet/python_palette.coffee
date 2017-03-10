({
    mode: 'python',
    modeOptions: {
      functions: {

        newLine: {
          'color': 'blue' }
       }
     },

    palette: [
      {
        name: 'Imports',
        color: 'red',
        blocks: [
          { block: 'import library_name' },
          { block: 'from library_name import library_package' }
        ]
      },

      {
        name: 'Variables',
        color: 'orange',
        blocks: [
          { block: 'intVariable = 1' },
          { block: 'floatVariable = 1.0' },
          { block: 'stringVariable = \\'This is a string!\\'' },
          { block: 'boolVariable = True' },
          { block: 'listVariable = [0, 1, 2, 3, 4, 5, 6]' },
          { block: 'tupleVariable = (\\'ABCD\\', 12345, 6.0)' },
          { block: 'dictVariable = {\\'Red\\', \\'Orange\\', \\'Yellow\\', \\'Green\\', \\'Blue\\', \\'Indigo\\', \\'Violet\\'}'  }
        ]
      },

      {
        name: 'Operators',
        color: 'yellow',
        blocks: [
          { block: 'a + b' },
          { block: 'a - b' },
          { block: 'a * b' },
          { block: 'a / b' },
          { block: 'a % b' },
          { block: 'a ** b' },
          { block: 'a // b' },
          { block: '(a + b)' },
          

          { block: 'a == b' },
          { block: 'a != b' },
          { block: 'a <> b' },
          { block: 'a > b' },
          { block: 'a < b' },
          { block: 'a >= b' },
          { block: 'a <= b' },
                 
          { block: 'a += b' },
          { block: 'a -= b' },
          { block: 'a *= b' },
          { block: 'a /= b' },
          { block: 'a %= b' },
          { block: 'a **= b' },
          { block: 'a //= b' },
                 
          { block: 'a \& b' },
          { block: 'a \| b' },
          { block: 'a \^ b' },
          { block: '\~a = b' },
          { block: 'a << 1' },
          { block: 'a >> 1' },
                      
          { block: 'a and b' },
          { block: 'a or b' },
          { block: 'not(a and b)' },
                 
          { block: 'a in b' },
          { block: 'a not in b' },
                 
          { block: 'a is b' },
          { block: 'a is not b' },
                 
          { block: 'True' },
          { block: 'False' }
        ]
      },

      {
        name: 'Controls',
        color: 'green',
        blocks: [
          { block: 'if a == b:\\n  print (\\'This is a conditional statement!\\')' },
          { block: 'while a == b:\\n  print (\\'This is a conditional loop!\\')' },
          { block: 'for i in list_variable:\\n  print (i)' },
          { block: 'break' },
          { block: 'continue' },
          { block: 'pass' }
        ]
      },

      {
        name: 'Functions',
        color: 'blue',
        blocks: [
          { block: 'def FunctionName(args):\\n  return' },
          { block: 'FunctionName(args)' },
          { block: 'lambda_variable = lambda args: args * 2' },
          { block: 'return return_value' },
          { block: 'return' }
        ]
      },

      {
        name: 'Classes',
        color: 'purple',
        blocks: [
          { block: 'class ClassName:\\n def __init__(self, args):\\n  self.args = args\\n  print(\\'NOTE: The self parameter is the instance that the method is called on!\\')\\n def __del__(self):\\n  class_name = self.__class__.__name__\\n  print(class_name + \\' was destroyed!\\')' },
          { block: 'class_object = ClassName(\\'This is the default constructors args parameter!\\')' },
          { block: 'class_object.__init__()' }
        ]
      },

      {
        name: 'Misc',
        color: 'black',
        blocks: [
          { block: '# this is a comment' },

        ]
      },
    ]
  })