import { CrtMonitor } from '~/components/crt-monitor';
import { RegisterStepper } from '~/components/register-stepper';

export default function Autocadastro() {

  return (
    <div className="crt-page">
      <CrtMonitor title="">
        <RegisterStepper />
        
      </CrtMonitor>
    </div>
  );
}
