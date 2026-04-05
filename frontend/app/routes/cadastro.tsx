import { useNavigate } from 'react-router';
import { CrtMonitor } from '~/components/crt-monitor';
import { RegisterStepper } from '~/components/register-stepper';

export default function Autocadastro() {
  const navigate = useNavigate();

  return (
    <div className="crt-page">
      <CrtMonitor title="">
        <RegisterStepper onComplete={() => navigate("/")} />
      </CrtMonitor>
    </div>
  );
}
