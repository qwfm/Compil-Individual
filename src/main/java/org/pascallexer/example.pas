program SumAndAverage;

var
    num, count, i, sum: integer;
    average: real;

begin
    sum := 0;
    count := 0;

    // Read the number of integers
    Write('Enter the number of integers: ');
    ReadLn(count);

    // Read each integer and calculate the sum
    for i := 1 to count do
    begin
        Write('Enter integer ', i, ': ');
        ReadLn(num);
        sum := sum + num;
    end;

    // Calculate the average
    if count > 0 then
        average := sum / count
    else
        average := 0;

    // Print the results
    WriteLn('The sum is: ', sum);
    WriteLn('The average is: ', average:0:2);  // 0:2 formats the average to 2 decimal places
end.