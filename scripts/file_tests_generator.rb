tests =  Dir
  .entries('src/main/resources/TestPrograms')
  .select{|file| !File.directory?(file)}
  .map do |file|
    if file.include?('bad') then
      expectation = 'fails'
      succeeds = false
    else
      expectation = 'noErrors'
      succeeds = true
    end
    %Q(
    @Test
    public void parseFile_#{file.gsub(/[\._]/, '')}_#{expectation}() throws Exception {
        testParseFile("#{file}", #{succeeds});
    }
    )
  end
  .join()


path = 'src/main/java/TestFiles.java'
file = File.open(path)

old_contents = file.read
new_contents = old_contents.gsub(
  /(\/\*\s*@inject\s+test\s+start\s*\*\/)[\s\S]*(    \/\*\s*@inject\s+test\s+end\s*\*\/)/,
  "\\1\n#{tests}\n\\2"
)

file.close
File.write(path, new_contents)
